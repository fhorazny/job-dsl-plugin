package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.triggers.TriggerContext
import javaposse.jobdsl.dsl.helpers.workflow.WorkflowDefinitionContext

class WorkflowJob extends Job {
    WorkflowJob(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    /**
     * Adds a workflow definition.
     */
    void definition(@DslContext(WorkflowDefinitionContext) Closure definitionClosure) {
        WorkflowDefinitionContext context = new WorkflowDefinitionContext(jobManagement, this)
        ContextHelper.executeInContext(definitionClosure, context)

        configure { Node project ->
            Node definition = project / definition
            if (definition) {
                project.remove(definition)
            }
            project << context.definitionNode
        }
    }

    @Deprecated
    void concurrentBuild(boolean allowConcurrentBuild = true) {
        configure { Node project ->
            Node node = methodMissing('concurrentBuild', allowConcurrentBuild)
            project / node
        }
    }

    @Deprecated
    void triggers(@DslContext(TriggerContext) Closure closure) {
        TriggerContext context = new TriggerContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        configure { Node project ->
            context.triggerNodes.each {
                project / 'triggers' << it
            }
        }
    }
}
