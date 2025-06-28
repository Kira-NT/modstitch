package dev.isxander.modstitch.base

import dev.isxander.modstitch.base.extensions.FinalMixinConfigurationSettings
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class AppendModMetadataTask : SourceTask() {
    @get:Input
    abstract val mixins: ListProperty<FinalMixinConfigurationSettings>

    @get:Input
    abstract val accessWideners: ListProperty<String>

    @TaskAction
    fun run() = source.visit { appendModMetadata(file) }

    abstract fun appendModMetadata(file: File)
}
