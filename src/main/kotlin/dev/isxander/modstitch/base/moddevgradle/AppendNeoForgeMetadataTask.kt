package dev.isxander.modstitch.base.moddevgradle

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.core.file.FileNotFoundAction
import com.electronwill.nightconfig.core.io.WritingMode
import com.electronwill.nightconfig.toml.TomlFormat
import dev.isxander.modstitch.base.AppendModMetadataTask
import java.io.File

abstract class AppendNeoForgeMetadataTask : AppendModMetadataTask() {
    override fun appendModMetadata(file: File) {
        if (file.extension != "toml") {
            error("Invalid file extension: ${file.extension}")
        }

        val config = TomlFormat.instance().createParser().parse(file, FileNotFoundAction.THROW_ERROR)
        appendNewEntries(config, "mixins", "config", mixins.get().map { it.config })
        appendNewEntries(config, "accessTransformers", "file", accessWideners.get())

        TomlFormat.instance().createWriter().write(config, file, WritingMode.REPLACE)
    }

    private fun appendNewEntries(config: Config, key: String, name: String, values: Iterable<String>) {
        val entries = config.getOptional<MutableList<Config>>(key).orElseGet {
            mutableListOf<Config>().also { config.set<MutableList<Config>>(key, it) }
        }
        val existingEntries = entries.map { it.getOptional<String>(name).orElse("") }

        values.forEach {
            if (existingEntries.contains(it)) {
                return@forEach
            }

            val entry = Config.inMemory()
            entry.set<String>(name, it)
            entries.add(entry)
        }
    }
}
