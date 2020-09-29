package com.luxoft.training.msa.easywallet

import com.luxoft.training.msa.easywallet.entity.Wallet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.reactive.server.WebTestClient

//@ActiveProfiles("dev")
@SpringBootTest
@AutoConfigureWebTestClient
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class EasyWalletAppTests(
        databaseClient: DatabaseClient,
        private val client: WebTestClient
) {
    init {
        val rowsUpdated = databaseClient.execute("create table if not exists wallet(id IDENTITY PRIMARY KEY, user_id VARCHAR NOT NULL, balance bigint NOT NULL)").fetch().rowsUpdated().block()!!
        println(rowsUpdated)
    }

    @Test
    fun testWalletsApi() {
        var targetWallet = createWallet()

        val amount = 65L
        var response = transferTo(-1, targetWallet.id!!, amount).expectStatus().is4xxClientError.expectBody(String::class.java).returnResult().responseBody!!
        assertTrue(response.contains("Incorrect source wallet"))
        targetWallet = getWallet(targetWallet.id!!)
        assertEquals(0, targetWallet.balance)

        var sourceWallet = createWallet()
        response = transferTo(sourceWallet.id!!, -1, amount).expectStatus().is4xxClientError.expectBody(String::class.java).returnResult().responseBody!!
        assertTrue(response.contains("Incorrect source wallet"))
        sourceWallet = getWallet(sourceWallet.id!!)
        assertEquals(0, sourceWallet.balance)

        replenish(sourceWallet.id!!, amount)
        sourceWallet = getWallet(sourceWallet.id!!)
        assertEquals(amount, sourceWallet.balance)

        response = transferTo(sourceWallet.id!!, -1, amount).expectStatus().is4xxClientError.expectBody(String::class.java).returnResult().responseBody!!
        assertTrue(response.contains("Incorrect target wallet"))
        sourceWallet = getWallet(sourceWallet.id!!)
        assertEquals(amount, sourceWallet.balance)

        transferTo(sourceWallet.id!!, targetWallet.id!!, amount).expectStatus().isOk
        sourceWallet = getWallet(sourceWallet.id!!)
        assertEquals(0, sourceWallet.balance)
        targetWallet = getWallet(targetWallet.id!!)
        assertEquals(amount, targetWallet.balance)

        deleteWallet(sourceWallet.id!!)
        deleteWallet(targetWallet.id!!)
    }

    private fun createWallet() = client.post().uri("/api/wallets").exchange().expectBody(Wallet::class.java).returnResult().responseBody!!

    private fun getWallet(id: Long) = client.get().uri("/api/wallets/{id}", id).exchange().expectBody(Wallet::class.java).returnResult().responseBody!!

    private fun replenish(id: Long, amount: Long) = client.post().uri("/api/wallets/{id}/replenish?amount={amount}", id, amount).exchange().expectStatus().isOk

    private fun transferTo(from: Long, to: Long, amount: Long) = client.post().uri("/api/wallets/{from}/transfer?to={to}&amount={amount}", from, to, amount).exchange()

    private fun deleteWallet(id: Long) = client.delete().uri("/api/wallets/{id}", id).exchange().expectStatus().isOk
}
