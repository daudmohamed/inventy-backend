package com.inventy.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BarcodeProductsDTO(
    @SerialName("products" ) var products : ArrayList<BarcodeProductDTO> = arrayListOf()
)


@Serializable
data class BarcodeProductDTO (
    @SerialName("barcode_number"          ) var barcodeNumber         : String?                 = null,
    @SerialName("barcode_formats"         ) var barcodeFormats        : String?                 = null,
    @SerialName("mpn"                     ) var mpn                   : String?                 = null,
    @SerialName("model"                   ) var model                 : String?                 = null,
    @SerialName("asin"                    ) var asin                  : String?                 = null,
    @SerialName("title"                   ) var title                 : String?                 = null,
    @SerialName("category"                ) var category              : String?                 = null,
    @SerialName("manufacturer"            ) var manufacturer          : String?                 = null,
    @SerialName("brand"                   ) var brand                 : String?                 = null,
    @SerialName("age_group"               ) var ageGroup              : String?                 = null,
    @SerialName("ingredients"             ) var ingredients           : String?                 = null,
    @SerialName("nutrition_facts"         ) var nutritionFacts        : String?                 = null,
    @SerialName("energy_efficiency_class" ) var energyEfficiencyClass : String?                 = null,
    @SerialName("color"                   ) var color                 : String?                 = null,
    @SerialName("gender"                  ) var gender                : String?                 = null,
    @SerialName("material"                ) var material              : String?                 = null,
    @SerialName("pattern"                 ) var pattern               : String?                 = null,
    @SerialName("format"                  ) var format                : String?                 = null,
    @SerialName("multipack"               ) var multipack             : String?                 = null,
    @SerialName("size"                    ) var size                  : String?                 = null,
    @SerialName("length"                  ) var length                : String?                 = null,
    @SerialName("width"                   ) var width                 : String?                 = null,
    @SerialName("height"                  ) var height                : String?                 = null,
    @SerialName("weight"                  ) var weight                : String?                 = null,
    @SerialName("release_date"            ) var releaseDate           : String?                 = null,
    @SerialName("description"             ) var description           : String?                 = null,
    @SerialName("features"                ) var features              : ArrayList<String>       = arrayListOf(),
    @SerialName("images"                  ) var images                : ArrayList<String>       = arrayListOf(),
    @SerialName("last_update"             ) var lastUpdate            : String?                 = null,
    @SerialName("reviews"                 ) var reviews               : ArrayList<String>       = arrayListOf()

)