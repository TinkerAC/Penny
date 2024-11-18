**根据您的架构，以下是从下到上各层级的功能，以及以插入记录和根据 ID 获取记录为例，各层级接收和返回的类型（包括远程数据源）。
**

---

### **1. 数据源层（Data Source）**

**功能：**

- **本地数据源（LocalDataSource）：** 使用 SQLDelight 自动生成的代码，与本地数据库直接交互，执行数据的增删改查操作。

- **远程数据源（RemoteDataSource）：** 通过网络请求，与远程服务器交互，获取或提交数据。

---

#### **插入记录（Insert Record）：**

- **本地数据源方法：**

    - **函数名：** `insertLedger(ledgerEntity: LedgerEntity)`

    - **接收类型：** `LedgerEntity`（SQLDelight 自动生成的数据库模型）

    - **返回类型：** `Unit` 或操作结果（如成功或失败的标志）

- **远程数据源方法：**

    - **函数名：** `uploadLedger(ledgerDTO: LedgerDTO): ResponseDTO`

    - **接收类型：** `LedgerDTO`（用于网络传输的数据模型）

    - **返回类型：** `ResponseDTO`（服务器返回的响应模型，包含操作结果）

---

#### **根据 ID 获取记录（GetByID）：**

- **本地数据源方法：**

    - **函数名：** `getLedgerById(id: Long): LedgerEntity?`

    - **接收类型：** `id: Long`

    - **返回类型：** `LedgerEntity?`（SQLDelight 自动生成的数据库模型，可为空）

- **远程数据源方法：**

    - **函数名：** `fetchLedgerById(id: Long): LedgerDTO`

    - **接收类型：** `id: Long`

    - **返回类型：** `LedgerDTO`（从服务器获取的 Ledger 数据传输对象）

---

### **2. 仓库层（Repository）**

**功能：**

- **职责：** 负责协调本地和远程数据源，处理数据获取和存储的逻辑。

- **模型转换：** 在数据源模型（如 `LedgerEntity`、`LedgerDTO`）和领域模型（`Ledger`）之间进行转换。

---

#### **插入记录（Insert Record）：**

- **仓库方法：**

    - **函数名：** `addLedger(ledger: Ledger)`

    - **接收类型：** `Ledger`（领域模型）

    - **处理过程：**

        1. **将领域模型转换为数据源模型：**

            - 将 `Ledger` 转换为 `LedgerEntity`，用于本地存储。

            - 将 `Ledger` 转换为 `LedgerDTO`，用于远程上传。

        2. **调用数据源方法：**

            - 调用本地数据源的 `insertLedger(ledgerEntity)` 方法，保存到本地数据库。

            - 调用远程数据源的 `uploadLedger(ledgerDTO)` 方法，上传到服务器。

    - **返回类型：** `Unit` 或操作结果（如成功或失败的标志）

---

#### **根据 ID 获取记录（GetByID）：**

- **仓库方法：**

    - **函数名：** `getLedgerById(id: Long): Ledger?`

    - **接收类型：** `id: Long`

    - **处理过程：**

        1. **尝试从本地数据源获取：**

            - 调用本地数据源的 `getLedgerById(id)` 方法，获取 `LedgerEntity`。

        2. **如果本地不存在，尝试从远程获取：**

            - 调用远程数据源的 `fetchLedgerById(id)` 方法，获取 `LedgerDTO`。

            - 将 `LedgerDTO` 转换为 `LedgerEntity`，并调用本地数据源的 `insertLedger(ledgerEntity)`
              方法，缓存到本地。

        3. **将数据源模型转换为领域模型：**

            - 将 `LedgerEntity` 转换为 `Ledger`，返回给上层。

    - **返回类型：** `Ledger?`（领域模型，可为空）

---

### **3. 用例层（Use Case）**

**功能：**

- **职责：** 实现具体的业务逻辑，调用仓库层的方法，完成用户操作或业务流程。

---

#### **插入记录（Insert Record）：**

- **用例方法：**

    - **函数名：** `execute(ledger: Ledger)`

    - **接收类型：** `Ledger`（领域模型）

    - **处理过程：**

        - 调用仓库的 `addLedger(ledger)` 方法，完成插入操作。

    - **返回类型：** `Unit` 或操作结果

---

#### **根据 ID 获取记录（GetByID）：**

- **用例方法：**

    - **函数名：** `execute(id: Long): Ledger?`

    - **接收类型：** `id: Long`

    - **处理过程：**

        - 调用仓库的 `getLedgerById(id)` 方法，获取 `Ledger`。

    - **返回类型：** `Ledger?`（领域模型，可为空）

---

### **4. 表现层（Presentation Layer，例如 ViewModel）**

**功能：**

- **职责：** 与 UI 交互，处理用户输入和输出，调用用例层的方法。

---

### **模型类型说明**

- **领域模型（Domain Model）：** `Ledger`

  ```kotlin
  data class Ledger(
      val id: Long,
      val name: String,
      val currencyCode: String,
      val createdAt: Long,
      val updatedAt: Long
  )
  ```

- **数据库模型（Data Model，本地数据源）：** `LedgerEntity`

  ```kotlin
  data class LedgerEntity(
      val id: Long,
      val name: String,
      val currency_code: String,
      val created_at: Long,
      val updated_at: Long
  )
  ```

- **数据传输对象（DTO，远程数据源）：** `LedgerDTO`

  ```kotlin
  data class LedgerDTO(
      val id: Long,
      val name: String,
      val currencyCode: String,
      val createdAt: Long,
      val updatedAt: Long
  )
  ```

- **响应数据模型（远程数据源）：** `ResponseDTO`

  ```kotlin
  data class ResponseDTO(
      val success: Boolean,
      val message: String?,
      val data: LedgerDTO?
  )
  ```

---

### **各层模型转换**

- **仓库层负责模型转换：**

    - **领域模型 ↔ 数据库模型（Ledger ↔ LedgerEntity）**

    - **领域模型 ↔ 数据传输对象（Ledger ↔ LedgerDTO）**

- **转换示例：**

  ```kotlin
  // Ledger 与 LedgerEntity 的转换
  fun ledgerToEntity(ledger: Ledger): LedgerEntity {
      return LedgerEntity(
          id = ledger.id,
          name = ledger.name,
          currency_code = ledger.currencyCode,
          created_at = ledger.createdAt,
          updated_at = ledger.updatedAt
      )
  }

  fun entityToLedger(entity: LedgerEntity): Ledger {
      return Ledger(
          id = entity.id,
          name = entity.name,
          currencyCode = entity.currency_code,
          createdAt = entity.created_at,
          updatedAt = entity.updated_at
      )
  }

  // Ledger 与 LedgerDTO 的转换
  fun ledgerToDTO(ledger: Ledger): LedgerDTO {
      return LedgerDTO(
          id = ledger.id,
          name = ledger.name,
          currencyCode = ledger.currencyCode,
          createdAt = ledger.createdAt,
          updatedAt = ledger.updatedAt
      )
  }

  fun dtoToLedger(dto: LedgerDTO): Ledger {
      return Ledger(
          id = dto.id,
          name = dto.name,
          currencyCode = dto.currencyCode,
          createdAt = dto.createdAt,
          updatedAt = dto.updatedAt
      )
  }
  ```

---

### **总结**

- **数据源层（Data Source）：**

    - **本地数据源：** 使用 SQLDelight，与本地数据库交互，接收和返回 `LedgerEntity`。

    - **远程数据源：** 通过网络请求，与服务器交互，接收和返回 `LedgerDTO` 和 `ResponseDTO`。

- **仓库层（Repository）：**

    - **功能：** 调用数据源的方法，处理业务逻辑，进行模型转换。

    - **接收和返回类型：** 使用领域模型 `Ledger`。

- **用例层（Use Case）：**

    - **功能：** 实现具体的业务操作，调用仓库的方法。

    - **接收和返回类型：** 使用领域模型 `Ledger`。

- **模型转换在仓库层完成，确保各层职责单一，保持代码的可维护性和可扩展性。**

---

**通过以上描述，您可以清晰地了解各个层级的功能，以及在插入记录和根据 ID
获取记录的过程中，各层级所接收和返回的类型，包括对远程数据源的处理。**