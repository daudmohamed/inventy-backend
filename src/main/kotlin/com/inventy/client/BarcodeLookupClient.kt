package com.inventy.client

import com.inventy.dto.BarcodeProductsDTO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class BarcodeLookupClient(val apiKey: String) {
    companion object {
        val client = HttpClient {
            defaultClient()
        }
    }

    suspend fun getBarcodeProduct(barcode: String): BarcodeProductsDTO {
        return client.get("https://api.barcodelookup.com/v3/products?barcode=$barcode&formatted=y&key=$apiKey")
            .body()
    }
}