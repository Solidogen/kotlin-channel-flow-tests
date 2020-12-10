import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

fun main() {
    SharedFlowApp()
    keepProcessAlive()
}

/**
 * Shared flow itself doesn't make it magically work. I need to take a cold flow and share it with shareIn
 * and set sharingStarted to lazily (see other sample)
 * */
class SharedFlowApp : CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Default

    private val sharedFlow: MutableSharedFlow<SampleItem> = MutableSharedFlow()
    private val flow: SharedFlow<SampleItem> = sharedFlow

    init {
        launch {
            logger.info("app init")
            subscribe()
            publish()
        }
    }

    private fun subscribe() {
        flow
            .onSubscription {
                logger.info("flow onSubscription")
            }
            .onStart {
                logger.info("flow onStart")
            }
            .onEach {
                logger.info("item collected $it")
            }
            .launchIn(this)
        logger.info("launchIn called")
    }

    private suspend fun publish() {
        repeat(1000) { i ->
            val sampleItem = SampleItem(i = i)
            logger.info("publishing item: $sampleItem")
            sharedFlow.emit(sampleItem)
        }
    }
}