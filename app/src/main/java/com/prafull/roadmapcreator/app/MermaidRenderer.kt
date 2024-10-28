package com.prafull.roadmapcreator.app

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView


private class MermaidRenderer {
    companion object {
        private const val MERMAID_CDN =
            "https://cdnjs.cloudflare.com/ajax/libs/mermaid/9.3.0/mermaid.min.js"

        fun generateHtml(diagram: String, theme: String = "default"): String = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <script src="$MERMAID_CDN"></script>
                <script>
                    mermaid.initialize({
                        startOnLoad: true,
                        theme: '$theme',
                        securityLevel: 'loose'
                    });
                </script>
                <style>
                    body { 
                        margin: 0; 
                        padding: 16px;
                        background-color: transparent;
                    }
                    .mermaid { 
                        width: 100%;
                        display: flex;
                        justify-content: center;
                    }
                </style>
            </head>
            <body>
                <pre class="mermaid">
                    $diagram
                </pre>
            </body>
            </html>
        """.trimIndent()
    }
}

@Composable
private fun MermaidDiagram(
    diagram: String,
    modifier: Modifier = Modifier,
    theme: String = "default",
    onLoadComplete: () -> Unit = {}
) {
    val context = LocalContext.current
    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.apply {
                javaScriptEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                displayZoomControls = false
            }
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    onLoadComplete()
                }
            }
            isHorizontalScrollBarEnabled = true
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }
    }

    DisposableEffect(diagram, theme) {
        webView.loadDataWithBaseURL(
            null,
            MermaidRenderer.generateHtml(diagram, theme),
            "text/html",
            "UTF-8",
            null
        )

        onDispose {
            webView.stopLoading()
            webView.destroy()
        }
    }

    AndroidView(
        factory = { webView },
        modifier = modifier
    )
}

// Optional: State holder for the diagram
class MermaidState(
    initialDiagram: String = "",
    initialTheme: String = "default"
) {
    var diagram by mutableStateOf(initialDiagram)
        private set

    var theme by mutableStateOf(initialTheme)
        private set

    fun updateDiagram(newDiagram: String) {
        diagram = newDiagram
    }

    fun updateTheme(newTheme: String) {
        theme = newTheme
    }
}

@Composable
fun rememberMermaidState(
    initialDiagram: String = "",
    initialTheme: String = "default"
): MermaidState = remember {
    MermaidState(initialDiagram, initialTheme)
}

// Usage Example
@Composable
fun Mermaid(string: String) {
    val mermaidState = rememberMermaidState(
        initialDiagram = string.trimIndent(),
        initialTheme = "default"
    )
    Column(Modifier.fillMaxSize()) {
        MermaidDiagram(
            diagram = mermaidState.diagram,
            theme = mermaidState.theme,
            modifier = Modifier.fillMaxSize(),
            onLoadComplete = {
                // Handle diagram load complete
            }
        )
    }
}