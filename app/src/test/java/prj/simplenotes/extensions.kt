package prj.simplenotes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
fun asyncTestInMain(action: suspend () -> Unit) {
    runTest {
        val testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        try {
            action()
        }
        finally {
            Dispatchers.resetMain()
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun runTestInMain(action: () -> Unit) {
    val testDispatcher = UnconfinedTestDispatcher()
    Dispatchers.setMain(testDispatcher)
    try {
        action()
    }
    finally {
        Dispatchers.resetMain()
    }
}