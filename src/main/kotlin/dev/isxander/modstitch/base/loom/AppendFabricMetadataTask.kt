package dev.isxander.modstitch.base.loom

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dev.isxander.modstitch.base.AppendModMetadataTask
import dev.isxander.modstitch.util.Side
import java.io.File

abstract class AppendFabricMetadataTask : AppendModMetadataTask() {
    override fun appendModMetadata(file: File) {
        if (file.extension != "json") {
            error("Invalid file extension: ${file.extension}")
        }

        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = file.reader().use { gson.fromJson(it, JsonObject::class.java) }

        val accessWidener = accessWideners.get().firstOrNull()
        if (accessWidener != null) {
            json.addProperty("accessWidener", accessWidener)
        }

        val mixinConfigs = json.getAsJsonArray("mixins") ?: JsonArray().also { json.add("mixins", it) }
        val existingMixinConfigs = mixinConfigs.map { when {
            it.isJsonObject -> it.asJsonObject.get("config")?.asString ?: ""
            it.isJsonPrimitive && it.asJsonPrimitive.isString -> it.asString
            else -> it.toString()
        }}
        mixins.get().forEach {
            if (existingMixinConfigs.contains(it.config)) {
                return@forEach
            }

            val mixinConfig = JsonObject()
            mixinConfig.addProperty("config", it.config)
            mixinConfig.addProperty("environment", when (it.side) {
                Side.Both -> "*"
                Side.Client -> "client"
                Side.Server -> "server"
            })
            mixinConfigs.add(mixinConfig)
        }

        return file.writer().use { gson.toJson(json, it) }
    }
}
