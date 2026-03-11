Chromium extension source assets for testRigor streaming.

Files:
- manifest.json
- service_worker.js
- content_bridge.js
- offscreen.html
- offscreen.js

By default, the library extracts these bundled unpacked assets to a temp dir
and loads them automatically with --load-extension.

Chrome tab capture requires a user gesture. If you see "Extension has not been
invoked for the current page", the test will try sending Ctrl+Shift+S once;
if that does not work (e.g. automation key events are not treated as gestures),
manually press Ctrl+Shift+S in the browser window once after the first get(url)
to start streaming, or run the browser non-headless and click the extension
icon once.

Optional override:
streaming.chromium.extension.crxPath=/absolute/path/to/unpacked-extension-directory
