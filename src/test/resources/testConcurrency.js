onePlusOne: function onePlusOne() {
    i = 0;
    i += 1;
    shortly_later = new Date() / 1000 + Math.random;
    while ((new Date() / 1000) < shortly_later) {
        Math.random()
    }
    i += 1;
    return i;
}
