package com.inventy.route

import com.inventy.client.BarcodeLookupClient
import com.inventy.dto.ItemDTO
import com.inventy.repository.ItemRepository
import com.inventy.repository.UserOwnedRepository.Companion.userContext
import com.inventy.model.Item
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*



fun Application.configureItemRoute(
    barcodeLookupClient: BarcodeLookupClient
) {

    routing {
        authenticate ("auth0") {
            // List items
            get("/item") {
                val items = userContext<List<ItemDTO>, ItemRepository>(call.authentication) { repository ->
                    return@userContext repository.list()
                        .map(Item::toDTO)
                }

                call.respond(HttpStatusCode.OK, items)
            }
            // Create item
            post("/item") {
                val itemDTO = call.receive<ItemDTO>()
                val id = userContext<Long, ItemRepository>(call.authentication) { repository ->
                    return@userContext repository.create(itemDTO)
                }
                call.respond(HttpStatusCode.Created, id)
            }
            // Read item
            get("/item/{id}") {
                val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
                val item = userContext<Item?, ItemRepository>(call.authentication) { repository ->
                    return@userContext repository.read(id)
                }
                if (item != null) {
                    call.respond(HttpStatusCode.OK, item.toDTO())
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            // Update item
            put("/item/{id}") {
                val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
                val itemDTO = call.receive<ItemDTO>()
                userContext<Unit, ItemRepository>(call.authentication) { repository ->
                    repository.update(id, itemDTO)
                }
                call.respond(HttpStatusCode.OK)
            }
            // Delete item
            delete("/item/{id}") {
                val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
                userContext<Unit, ItemRepository>(call.authentication) { repository ->
                    repository.delete(id)
                }
                call.respond(HttpStatusCode.OK)
            }

            // List shopping items
            get("/shopping") {
                val items = userContext<List<ItemDTO>, ItemRepository>(call.authentication) { repository ->
                    return@userContext repository.shoppingList()
                        .map(Item::toDTO)
                }
                call.respond(HttpStatusCode.OK, items)
            }

            post("/ean/{ean}") {
                val ean = call.parameters["ean"] ?: throw IllegalArgumentException("Invalid EAN")
                val products = barcodeLookupClient.getBarcodeProduct(ean).products
                if (products.isEmpty()) {
                    call.respond(HttpStatusCode.NotFound)
                    return@post
                }
                val id = userContext<Long, ItemRepository>(call.authentication) { repository ->
                    return@userContext repository.create(ItemDTO(
                        name = products[0].title ?: "",
                        current = 0,
                        target = 1
                    ))
                }
                call.respond(HttpStatusCode.Created, id)
            }
        }
    }
}