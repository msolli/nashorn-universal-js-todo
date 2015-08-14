* lag interface med alle render-funksjonene i JS som skal være tilgjengelig i Java
    - https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/prog_guide/api.html - eksempel 7
    - men se også http://stackoverflow.com/questions/31669566/nashorn-javascript-invocable-getinterface-fails-across-classloaders-in-web-app
* sett opp webpack til å lage et objekt med alle render-funksjonene. Dette objektet implementerer interfacet over.
* doRender() i NashornRenderer må endres til å bruke dette interfacet


TODO

* Reload av Java/template-kode fra IDEA
* Mål tid brukt på hvert metodekall, og initiell lasting av JS
* Hvor initieres Nashorn to ganger ved oppstart?
* Legg til @Value("${isJsReloadingEnabled}") boolean isReloadingEnabled i JsComponents
* Sett input og output på npm_run_build-task
* Lag TodoMvc med routing og redux
* Timeboxing av metodekall
