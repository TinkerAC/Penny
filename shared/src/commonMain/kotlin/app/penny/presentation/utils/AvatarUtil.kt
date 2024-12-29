package app.penny.presentation.utils

import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.core.toByteArray


/**
 * SHA-256 Algorithm in Pure Kotlin
 */
object SHA256 {

    private val K = listOf(
        0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5,
        0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
        0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3,
        0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
        0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc,
        0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
        0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7,
        0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
        0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
        0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
        0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3,
        0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
        0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5,
        0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
        0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208,
        0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    )

    /**
     * 生成给定字符串的SHA-256哈希值。
     *
     * @param input 要进行哈希处理的输入字符串。
     * @return 输入字符串的SHA-256哈希值，以十六进制格式表示（纯字母数字）。
     */
    fun hash(input: String): String {
        val bytes = input.toByteArray(Charsets.UTF_8)
        val padded = padMessage(bytes)
        val blocks = splitIntoBlocks(padded)
        val hash = processBlocks(blocks)
        return hash.joinToString("") { intToHex(it) }
    }

    /**
     * 对消息进行填充，以符合SHA-256的规范。
     *
     * @param message 原始消息的字节数组。
     * @return 填充后的消息字节数组。
     */
    private fun padMessage(message: ByteArray): ByteArray {
        val originalLength = message.size * 8L
        // 添加一个'1'位（即0x80）
        val withOne = message + 0x80.toByte()
        // 计算需要填充的'0'字节数，使得消息长度 ≡ 448 mod 512（即56字节 mod 64字节）
        val paddingLength = ((56 - (withOne.size % 64)) + 64) % 64
        val padding = ByteArray(paddingLength) { 0x00 }
        // 添加填充的'0'字节
        val withPadding = withOne + padding
        // 添加64位的原始消息长度（大端序）
        val lengthBytes = longToBytes(originalLength)
        return withPadding + lengthBytes
    }

    /**
     * 将长整型转换为字节数组（大端序）。
     *
     * @param value 要转换的长整型值。
     * @return 转换后的字节数组。
     */
    private fun longToBytes(value: Long): ByteArray {
        return ByteArray(8) {
            ((value shr (56 - it * 8)) and 0xff).toByte()
        }
    }

    /**
     * 将填充后的消息分割为512位（64字节）的块。
     *
     * @param padded 填充后的消息字节数组。
     * @return 分割后的消息块列表，每个块是一个包含16个32位整数的数组。
     */
    private fun splitIntoBlocks(padded: ByteArray): List<IntArray> {
        val blocks = mutableListOf<IntArray>()
        for (i in padded.indices step 64) {
            val block = IntArray(16)
            for (j in 0 until 16) {
                val index = i + j * 4
                block[j] = ((padded[index].toInt() and 0xff) shl 24) or
                        ((padded[index + 1].toInt() and 0xff) shl 16) or
                        ((padded[index + 2].toInt() and 0xff) shl 8) or
                        ((padded[index + 3].toInt() and 0xff))
            }
            blocks.add(block)
        }
        return blocks
    }

    /**
     * 处理所有的512位块，返回最终的哈希值。
     *
     * @param blocks 消息块列表。
     * @return 哈希值数组，包含8个32位整数。
     */
    private fun processBlocks(blocks: List<IntArray>): IntArray {
        var h0 = 0x6a09e667
        var h1 = 0xbb67ae85
        var h2 = 0x3c6ef372
        var h3 = 0xa54ff53a
        var h4 = 0x510e527f
        var h5 = 0x9b05688c
        var h6 = 0x1f83d9ab
        var h7 = 0x5be0cd19

        for (block in blocks) {
            val w = IntArray(64)
            // 拷贝前16个字
            for (i in 0..15) {
                w[i] = block[i]
            }
            // 扩展消息
            for (i in 16..63) {
                w[i] = sigma1(w[i - 2]) + w[i - 7] + sigma0(w[i - 15]) + w[i - 16]
            }

            var a = h0
            var b = h1
            var c = h2
            var d = h3
            var e = h4
            var f = h5
            var g = h6
            var h = h7

            for (i in 0..63) {
                val S1 = bigSigma1(e)
                val ch = choose(e, f, g)
                val temp1 = h + S1 + ch + K[i] + w[i]
                val S0 = bigSigma0(a)
                val maj = majority(a, b, c)
                val temp2 = S0 + maj

                h = g
                g = f.toInt()
                f = e.toLong()
                e = (d + temp1).toInt()
                d = c.toLong()
                c = b.toInt()
                b = a.toLong()
                a = (temp1 + temp2).toInt()
            }

            h0 += a
            h1 += b
            h2 += c
            h3 += d
            h4 += e
            h5 += f
            h6 += g
            h7 += h
        }

        return intArrayOf(h0, h1.toInt(), h2, h3.toInt(), h4, h5.toInt(), h6, h7)
    }

    // 选择函数
    private fun choose(x: Int, y: Long, z: Int): Int {
        return (x and y.toInt()) xor ((x.inv()) and z)
    }

    // 多数函数
    private fun majority(x: Int, y: Long, z: Int): Int {
        return (x and y.toInt()) xor (x and z) xor ((y and z.toLong()).toInt())
    }

    // 大Sigma0函数
    private fun bigSigma0(x: Int): Int {
        return rotateRight(x, 2) xor rotateRight(x, 13) xor rotateRight(x, 22)
    }

    // 大Sigma1函数
    private fun bigSigma1(x: Int): Int {
        return rotateRight(x, 6) xor rotateRight(x, 11) xor rotateRight(x, 25)
    }

    // 小Sigma0函数
    private fun sigma0(x: Int): Int {
        return rotateRight(x, 7) xor rotateRight(x, 18) xor (x ushr 3)
    }

    // 小Sigma1函数
    private fun sigma1(x: Int): Int {
        return rotateRight(x, 17) xor rotateRight(x, 19) xor (x ushr 10)
    }

    // 右旋转函数
    private fun rotateRight(x: Int, n: Int): Int {
        return (x ushr n) or (x shl (32 - n))
    }

    /**
     * 将32位整数转换为8位十六进制字符串（纯字母数字）。
     *
     * @param value 要转换的32位整数。
     * @return 转换后的8位十六进制字符串。
     */
    private fun intToHex(value: Int): String {
        val hexChars = "0123456789abcdef"
        val chars = CharArray(8)
        for (i in 0..7) {
            chars[7 - i] = hexChars[(value ushr (i * 4)) and 0xF]
        }
        return chars.concatToString()
    }
}


/**
 * Generate Gravatar URL based on the email address.
 */

fun generateGravatarUrl(email: String): String {
    val hash = SHA256.hash(email.trim().lowercase())
    return "https://www.gravatar.com/avatar/$hash?d=404"
}
