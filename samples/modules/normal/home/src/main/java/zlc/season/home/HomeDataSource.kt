package zlc.season.home

import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import zlc.season.yasha.YashaDataSource
import zlc.season.yasha.YashaItem

class HomeDataSource(scope: CoroutineScope) : YashaDataSource(scope) {
    override suspend fun loadInitial(): List<YashaItem>? {
        val header = Json.decodeFromString(HeaderItem.serializer(), headerJson)
        addHeader(header)

        addHeader(TitleItem())

        return Json.decodeFromString(ListSerializer(GoodsItem.serializer()), dataJson)
    }
}

@Serializable
data class GoodsItem(val img: String, val name: String, val money: String) : YashaItem

@Serializable
data class HeaderItem(val img: String, val name: String, val money: String) : YashaItem

@Serializable
data class TitleItem(val title: String = "") : YashaItem

val headerJson = """
    {
        "name":"Pure Balance Grain-Free Formula Adult Wet Dog Food, Beef & Chicken, 12.5 oz, 6 Pack",
        "money":"${'$'}34.44",
        "img":"https://i5.walmartimages.com/asr/46734d03-4932-4a17-b6c2-ec0aefc0bb7c_1.41092a676899ccb14bebe2c27a13e944.jpeg?odnHeight=612&odnWidth=612&odnBg=FFFFFF"
    }
""".trimIndent()

val dataJson = """
    [
      {
        "name": "Jinx Chicken, Brown Rice & Sweet Potato Dry Dog Food, 23.5 lb. Bag ",
        "money": "${'$'}10.88",
        "img": "https://i5.walmartimages.com/asr/ff5b6287-c941-4404-82ef-d22c374c8b37.813ace28cfd07bfad500c8d9edb6a9f6.jpeg?odnHeight=580&odnWidth=580&odnBg=FFFFFF"
      },
      {
        "name": "Jinx Salmon, Sweet Potato & Carrot Dry Dog Food, Grain Free, 23.5 lb. Bag ",
        "money": "${'$'}10.88",
        "img": "https://i5.walmartimages.com/asr/ea41b9a9-0f1f-44f1-a4e6-cd3e2860f089.b2e5060a49e292aeb37378bce513a546.jpeg?odnHeight=580&odnWidth=580&odnBg=FFFFFF"
      },
      {
        "name": "IAMS Adult High Protein Large Breed Dry Dog Food with Real Chicken, 15 lb. Bag ",
        "money": "${'$'}20.98",
        "img": "https://i5.walmartimages.com/asr/c6aad601-255f-41df-8390-8def3129c00b.abe04bd6077e9c334d20f081c88e8cf0.jpeg?odnHeight=580&odnWidth=580&odnBg=FFFFFF"
      },
      {
        "name": "Jinx Chicken, Sweet Potato & Carrot Dry Dog Food, Grain Free, 23.5 lb. Bag ",
        "money": "${'$'}25.98",
        "img": "https://i5.walmartimages.com/asr/1a07b1d7-fcc1-4fa8-98bb-58b509df294c.42c4e7e5c198d6236dcf713a56c6716f.jpeg?odnHeight=580&odnWidth=580&odnBg=FFFFFF"
      },
      {
        "name": "Jinx Salmon, Brown Rice & Sweet Potato Flavor Dry Dog Food, 23.5 lb. Bag ",
        "money": "${'$'}10.88",
        "img": "https://i5.walmartimages.com/asr/07c11a26-9040-4cae-b140-d5fcf979a9f9.2b337c93248b6d008982b9b30aa3a9a4.jpeg?odnHeight=580&odnWidth=580&odnBg=FFFFFF"
      },
      {
        "name": "Pure Balance Grain-Free Salmon & Pea Recipe Dry Dog Food ",
        "money": "${'$'}5.94",
        "img": "https://i5.walmartimages.com/asr/f080faea-0371-439d-9bd3-31b54ff913f7_1.9f899e0bbb9f9c49f2339c4309a1bcb3.jpeg?odnHeight=580&odnWidth=580&odnBg=FFFFFF"
      },
      {
        "name": "Purina Beneful Healthy Weight With Real Chicken Adult Dry Dog Food (Various Sizes) ",
        "money": "${'$'}5.98",
        "img": "https://i5.walmartimages.com/asr/e5888ca9-0f67-4381-b078-950ec79f6689.80c28f99f0cb9d4948def70f11350559.jpeg?odnHeight=580&odnWidth=580&odnBg=FFFFFF"
      },
      {
        "name": "Purina One Natural SmartBlend Chicken & Rice Formula Dry Dog Food (Various Sizes) ",
        "money": "${'$'}13.60",
        "img": "https://i5.walmartimages.com/asr/c78f83f6-4044-4089-b1b0-15fd173aec06.24a7f59f377e3d3d9977dcfccdc74e1b.jpeg?odnHeight=580&odnWidth=580&odnBg=FFFFFF"
      },
      {
        "name": "Purina ONE Natural Large Breed Dry Dog Food; SmartBlend Large Breed Adult Formula ",
        "money": "${'$'}24.02",
        "img": "https://i5.walmartimages.com/asr/aaa18960-516e-474d-be33-b31dd9f86994.91f7e8f9fdac63c4c60069ceba7280ec.jpeg?odnHeight=580&odnWidth=580&odnBg=FFFFFF"
      }
    ]
""".trimIndent()