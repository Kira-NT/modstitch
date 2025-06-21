package dev.isxander.modstitch.util

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import java.io.File

internal val Project.sourceSets: SourceSetContainer?
    get() = extensions.findByName("sourceSets") as SourceSetContainer?

internal val Project.mainSourceSet: SourceSet?
    get() = sourceSets?.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

internal val Project.mainSourceSetResources: List<File>
    get() = mainSourceSet?.resources?.srcDirs?.toList() ?: listOf()

internal fun Project.afterSuccessfulEvaluate(action: Action<Project>) = project.afterEvaluate {
    if (state.failure == null) {
        action.execute(this)
    }
}
