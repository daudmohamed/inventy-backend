package com.inventy.routes

import com.inventy.client.BarcodeLookupClient
import com.inventy.dto.ItemDTO
import com.inventy.repository.ItemService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database

fun Application.configureItemRoute(
    database: Database,
    barcodeLookupClient: BarcodeLookupClient
) {
    val itemService = ItemService(database)

    routing {
        // List items
        get("/item") {
            val items = itemService.list()
            call.respond(HttpStatusCode.OK, items)
        }
        // Create item
        post("/item") {
            val itemDTO = call.receive<ItemDTO>()
            val id = itemService.create(itemDTO)
            call.respond(HttpStatusCode.Created, id)
        }
        // Read item
        get("/item/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            val item = itemService.read(id)
            if (item != null) {
                call.respond(HttpStatusCode.OK, item)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        // Update item
        put("/item/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            val itemDTO = call.receive<ItemDTO>()
            itemService.update(id, itemDTO)
            call.respond(HttpStatusCode.OK)
        }
        // Delete item
        delete("/item/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            itemService.delete(id)
            call.respond(HttpStatusCode.OK)
        }

        // List shopping items
        get("/shopping") {
            val items = itemService.shoppingList()
            call.respond(HttpStatusCode.OK, items)
        }

        post("/ean/{ean}") {
            val ean = call.parameters["ean"] ?: throw IllegalArgumentException("Invalid EAN")
            val products = barcodeLookupClient.getBarcodeProduct(ean).products
            if (products.isEmpty()) {
                call.respond(HttpStatusCode.NotFound)
                return@post
            }
            val item = ItemDTO(
                name = products.get(0).title?:"",
                current = 0,
                target = 1
            )
            val id = itemService.create(item)
            call.respond(HttpStatusCode.Created, id)
        }
    }
}