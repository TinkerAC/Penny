// file: src/commonMain/kotlin/app/penny/feature/transactionDetail/TransactionDetailViewModel.kt
package app.penny.feature.transactionDetail

import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.TransactionRepository
import app.penny.core.domain.enum.Category
import app.penny.core.domain.enum.TransactionType
import app.penny.shared.SharedRes
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class TransactionDetailViewModel @OptIn(ExperimentalUuidApi::class) constructor(
    private val transactionUuid: Uuid,
    private val transactionRepository: TransactionRepository,
    private val ledgerRepository: LedgerRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(TransactionDetailUiState())
    val uiState = _uiState.asStateFlow()

    // 用来发送只读/错误等提示事件
    private val _eventFlow = MutableSharedFlow<TransactionDetailUiEvent>(replay = 0)
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        screenModelScope.launch {
            Logger.d("find transaction by uuid: $transactionUuid")
            val transaction = transactionRepository.findByUuid(transactionUuid)
            val belongingLedger = ledgerRepository.findByUuid(transaction!!.ledgerUuid)

            // 初始化 uiState
            _uiState.value = _uiState.value.copy(
                transactionModel = transaction,
                belongingLedger = belongingLedger!!,
                // 初始化时，把 transactionModel.amount 填充到 amountText + validatedAmount
                amountText = transaction.amount.toPlainString(),
                validatedAmount = transaction.amount
            )

            Logger.d("transaction: $transaction")
            Logger.d("TransactionDetailModel: ${_uiState.value.transactionModel}")

            //prepare default alternativeTransactionPrimaryCategory
            val primaryCategories = Category.getSubCategories(
                when (transaction.transactionType) {
                    TransactionType.EXPENSE -> Category.EXPENSE
                    TransactionType.INCOME -> Category.INCOME
                }
            )


            val secondaryCategories = Category.getSubCategories(transaction.category.parentCategory!!)

            _uiState.update { current ->
                current.copy(
                    alternativeTransactionPrimaryCategory = primaryCategories,
                    alternativeTransactionSecondaryCategory = secondaryCategories
                )
            }
        }
    }

    fun handleIntent(intent: TransactionDetailIntent) {
        when (intent) {

            // 1. 分类相关
            is TransactionDetailIntent.SelectPrimaryCategory -> {
                selectPrimaryCategory(intent.category)
            }

            is TransactionDetailIntent.SelectSecondaryCategory -> {
                _uiState.update {
                    it.copy(
                        transactionModel = it.transactionModel.copy(
                            category = intent.category
                        )
                    )
                }
            }

            is TransactionDetailIntent.SelectTransactionType -> {
                selectTransactionType(intent.transactionType)
            }

            // 2. 保存/删除
            is TransactionDetailIntent.SaveTransaction -> {
                // 点击“保存”时，再次校验金额
                if (!validateAmount()) {
                    // 校验失败，直接返回，不执行后续保存操作
                    return
                }

                // 校验通过 => 执行保存
                Logger.d("validatedAmount: ${_uiState.value.validatedAmount}")

                val rounded = _uiState.value.validatedAmount.divide(
                    BigDecimal.ONE,
                    DecimalMode(
                        decimalPrecision = 100,
                        roundingMode = RoundingMode.ROUND_HALF_AWAY_FROM_ZERO,
                        scale = 2
                    )
                )
                Logger.d("round to 2 decimal places: $rounded")

                val transaction = _uiState.value.transactionModel.copy(
                    amount = _uiState.value.validatedAmount.divide(
                        BigDecimal.ONE,
                        DecimalMode(
                            decimalPrecision = 100,
                            roundingMode = RoundingMode.ROUND_HALF_AWAY_FROM_ZERO,
                            scale = 2
                        )
                    )
                )
                _uiState.update {
                    it.copy(transactionModel = transaction)
                }

                screenModelScope.launch {
                    try {
                        transactionRepository.update(
                            _uiState.value.transactionModel
                        )
                        _eventFlow.emit(
                            TransactionDetailUiEvent.ShowSnackBar(
                                SharedRes.strings.transaction_saved_successfully
                            )
                        )
                    } catch (e: Exception) {
                        _eventFlow.emit(
                            TransactionDetailUiEvent.ShowSnackBar(
                                SharedRes.strings.failed_to_save_transaction
                            )
                        )
                    }
                }
            }

            is TransactionDetailIntent.DeleteTransaction -> {
                screenModelScope.launch {
                    try {
                        transactionRepository.deleteByUuid(transactionUuid)
                        _eventFlow.emit(
                            TransactionDetailUiEvent.ShowSnackBar(
                                SharedRes.strings.transaction_deleted_successfully
                            )
                        )
                    } catch (e: Exception) {
                        _eventFlow.emit(
                            TransactionDetailUiEvent.ShowSnackBar(
                                SharedRes.strings.failed_to_delete_transaction
                            )
                        )
                    }
                }
            }

            // 3. 备注
            is TransactionDetailIntent.UpdateRemark -> {
                _uiState.update { current ->
                    current.copy(
                        transactionModel = current.transactionModel.copy(
                            remark = intent.remark
                        )
                    )
                }
            }

            // 4. 金额相关
            is TransactionDetailIntent.UpdateAmountText -> {
                // 只更新字符串，清空错误
                _uiState.update { current ->
                    current.copy(
                        amountText = intent.text,
                        amountInputError = null
                    )
                }
            }

            // 原先 ValidateAmount Intent
            TransactionDetailIntent.ValidateAmount -> {
                validateAmount() // 直接调用我们提取的方法
            }

            // 5. 只读字段提示
            TransactionDetailIntent.ClickTimeField -> {
                screenModelScope.launch {
                    _eventFlow.emit(
                        TransactionDetailUiEvent.ShowSnackBar(SharedRes.strings.time_field_is_read_only)
                    )
                }
            }

            TransactionDetailIntent.ClickLedgerField -> {
                screenModelScope.launch {
                    _eventFlow.emit(
                        TransactionDetailUiEvent.ShowSnackBar(SharedRes.strings.ledger_field_is_read_only)
                    )
                }
            }
        }
    }

    /**
     * 执行金额校验逻辑：
     * 1. 解析 amountText
     * 2. 若格式错误或金额 <= 0，更新 error 并触发聚焦
     * 3. 成功时更新 validatedAmount 并清空 error
     *
     * @return true 表示校验通过，false 表示校验失败
     */
    private fun validateAmount(): Boolean {
        val amountText = _uiState.value.amountText
        val parseResult = runCatching { BigDecimal.parseString(amountText) }

        if (parseResult.isFailure) {
            // 解析失败，如仅有 "." 或 "12." 在某些库下会异常
            _uiState.update {
                it.copy(
                    amountInputError = SharedRes.strings.invalid_amount_format
                )
            }
            // 重新focus
            screenModelScope.launch {
                _eventFlow.emit(TransactionDetailUiEvent.FocusAmountInput(true))
            }
            return false
        }

        val parsedValue = parseResult.getOrThrow()
        if (parsedValue <= BigDecimal.ZERO) {
            // 金额必须 > 0
            _uiState.update {
                it.copy(
                    amountInputError = SharedRes.strings.amount_must_be_positive
                )
            }
            // 也可在此再发射 FocusAmountInput
            screenModelScope.launch {
                _eventFlow.emit(TransactionDetailUiEvent.FocusAmountInput(true))
            }
            return false
        }

        // 校验通过，更新 validatedAmount
        _uiState.update {
            it.copy(
                validatedAmount = parsedValue,
                amountInputError = null,
                transactionModel = it.transactionModel.copy(
                    amount = parsedValue
                )
            )
        }
        return true
    }

    /**
     * 选主分类
     */
    private fun selectPrimaryCategory(category: Category) {
        _uiState.update {
            val secondaryCategory = Category.getSubCategories(category)
            it.copy(
                alternativeTransactionSecondaryCategory = secondaryCategory,
                transactionModel = it.transactionModel.copy(
                    category = secondaryCategory.first()
                )
            )
        }
    }

    /**
     * 选交易类型
     */
    private fun selectTransactionType(transactionType: TransactionType) {
        val primaryCategories = Category.getSubCategories(
            when (transactionType) {
                TransactionType.EXPENSE -> Category.EXPENSE
                TransactionType.INCOME -> Category.INCOME
            }
        )

        _uiState.update { current ->
            current.copy(
                alternativeTransactionPrimaryCategory = primaryCategories,
                transactionModel = current.transactionModel.copy(
                    transactionType = transactionType,
                )
            )
        }

        selectPrimaryCategory(primaryCategories.first())
    }
}