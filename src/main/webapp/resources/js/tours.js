function sort() {
    var params = getParameters();

    if(params.price === 'asc') {
        params.price = 'desc';
    } else if (params.price === 'desc') {
        params.price = undefined;
    } else {
        params.price = 'asc';
    }

    goQueryParameters(params);
}

function setTourType(val) {
    var params = getParameters();
    toggleQueryParameter('type', val, params);
    goQueryParameters(params);
}