* lag interface med alle render-funksjonene i JS som skal være tilgjengelig i Java
    - https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/prog_guide/api.html - eksempel 7
    - men se også http://stackoverflow.com/questions/31669566/nashorn-javascript-invocable-getinterface-fails-across-classloaders-in-web-app
* sett opp webpack til å lage et objekt med alle render-funksjonene. Dette objektet implementerer interfacet over.
* doRender() i NashornRenderer må endres til å bruke dette interfacet



controlleren får en service autowired. denne servicen
