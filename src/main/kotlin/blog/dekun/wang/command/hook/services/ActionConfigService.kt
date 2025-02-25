package blog.dekun.wang.command.hook.services

import blog.dekun.wang.command.hook.constants.Constant
import blog.dekun.wang.command.hook.data.ActionConfig
import blog.dekun.wang.command.hook.data.TemplateConfig
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.annotations.XCollection


abstract class BasePersistentConfig<T> : PersistentStateComponent<BasePersistentConfig.State<T>> {

    data class State<T>(@XCollection(elementTypes = [ActionConfig::class, TemplateConfig::class]) var data: MutableList<T> = mutableListOf())

    private var state = State<T>()

    override fun getState() = state
    fun data(): MutableList<T> {
        return getState().data
    }

    override fun loadState(state: State<T>) {
        this.state = state
    }

    fun loadState(data: MutableList<T>) {
        loadState(State(data))
    }

}

@Service(Service.Level.APP)
@State(name = Constant.STATE_NAME, storages = [Storage(Constant.APP_CONFIG_XML_FILE_NAME)])
class ActionConfigAppState : BasePersistentConfig<ActionConfig>()

@Service(Service.Level.PROJECT)
@State(name = Constant.STATE_NAME, storages = [Storage(Constant.WORKSPACE_CONFIG_XML_FILE_NAME)])
class ActionConfigWorkspaceState : BasePersistentConfig<ActionConfig>()

@Service(Service.Level.APP)
@State(name = Constant.STATE_NAME_TEMPLATE_COMMAND, storages = [Storage(Constant.APP_CONFIG_XML_FILE_NAME)])
class CommandTemplateAppState : BasePersistentConfig<TemplateConfig>()

@Service(Service.Level.PROJECT)
@State(name = Constant.STATE_NAME_TEMPLATE_COMMAND, storages = [Storage(Constant.WORKSPACE_CONFIG_XML_FILE_NAME)])
class CommandTemplateWorkspaceState : BasePersistentConfig<TemplateConfig>()

@Service(Service.Level.APP)
@State(name = Constant.STATE_NAME_TEMPLATE_PARAM, storages = [Storage(Constant.APP_CONFIG_XML_FILE_NAME)])
class ParamTemplateAppState : BasePersistentConfig<TemplateConfig>()

@Service(Service.Level.PROJECT)
@State(name = Constant.STATE_NAME_TEMPLATE_PARAM, storages = [Storage(Constant.WORKSPACE_CONFIG_XML_FILE_NAME)])
class ParamTemplateWorkspaceState : BasePersistentConfig<TemplateConfig>()


class ActionConfigService private constructor() {

    companion object {

        private inline fun <reified T> getAppService(): T {
            return ApplicationManager.getApplication().getService(T::class.java)
        }

        private inline fun <reified T> getProjectService(project: Project): T {
            return project.getService(T::class.java)
        }

        fun getConfigs(project: Project?): List<ActionConfig> {
            val appConfig = getAppService<ActionConfigAppState>().data()
            val workspaceConfig = project?.let { getProjectService<ActionConfigWorkspaceState>(it).data() } ?: emptyList()
            return (appConfig + workspaceConfig).sortedBy { it.index }
        }

        fun saveConfigs(project: Project?, configs: List<ActionConfig>) {
            val (appConfig, workspaceConfig) = configs.partitionIndexed { !it.onlyProject }
            getAppService<ActionConfigAppState>().loadState(appConfig)
            project?.let { getProjectService<ActionConfigWorkspaceState>(it).loadState(workspaceConfig) }
        }

        private inline fun <reified A, reified W> getTemplates(project: Project?): List<TemplateConfig> where A : BasePersistentConfig<TemplateConfig>, W : BasePersistentConfig<TemplateConfig> {
            val app = getAppService<A>().data()
            val workspace = project?.let { getProjectService<W>(it).data() } ?: emptyList()
            return (app + workspace).sortedBy { it.index }
        }

        private inline fun <reified A, reified W> saveTemplates(project: Project?, templates: List<TemplateConfig>) where A : BasePersistentConfig<TemplateConfig>, W : BasePersistentConfig<TemplateConfig> {
            val (app, workspace) = templates.partitionIndexed { !it.onlyProject }
            getAppService<A>().loadState(app)
            project?.let { getProjectService<W>(it).loadState(workspace) }
        }

        fun getParamTemplates(project: Project?) = getTemplates<ParamTemplateAppState, ParamTemplateWorkspaceState>(project)

        fun saveParamTemplates(project: Project?, templates: List<TemplateConfig>) = saveTemplates<ParamTemplateAppState, ParamTemplateWorkspaceState>(project, templates)

        fun getCommandTemplates(project: Project?) = getTemplates<CommandTemplateAppState, CommandTemplateWorkspaceState>(project)

        fun saveCommandTemplates(project: Project?, templates: List<TemplateConfig>) = saveTemplates<CommandTemplateAppState, CommandTemplateWorkspaceState>(project, templates)

        private inline fun <T> List<T>.partitionIndexed(predicate: (T) -> Boolean): Pair<MutableList<T>, MutableList<T>> {
            val first = mutableListOf<T>()
            val second = mutableListOf<T>()
            filterNotNull().forEachIndexed { index, item ->
                if (item is TemplateConfig) item.index = index
                if (item is ActionConfig) item.index = index
                if (predicate(item)) first.add(item) else second.add(item)
            }
            return Pair(first, second)
        }
    }
}