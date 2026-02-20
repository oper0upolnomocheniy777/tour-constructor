function goQueryParameters(params) {
	var link = window.location.href;
	link = link.substring(0, window.location.href.indexOf('?'));
	window.location.href = link + serializeQueryParameters(params);
}

function overrideQueryParameter(param, val, params) {
	if(!params) {
		params = getParameters();
	}

	params[param] = val;
	return params;
}

function toggleQueryParameter(param, val, params) {
	if(!params) {
		params = getParameters();
	}

	var values = makeArray(params[param]);
	var ind = values.indexOf(val);

	if(ind === -1) {
		values.push(val);
	} else {
		values.splice(ind, 1);
	}
	params[param] = values;

	return params;
}

function getParameters() {
	var regex = /[?&]([^=#]+)=([^&#]*)/g;
    var url = window.location.href;
    var params = {};
    var matrch;

	while(match = regex.exec(url)) {
	    params[match[1]] = match[2];
	}

	for(var param in params) {
		if(params.hasOwnProperty(param)) {
			params[param] = deArray(params[param].split(','));
		}
	}

	return params;
}

function serializeQueryParameters(params) {
	var ans = '?';
	for(var param in params) {
		if(params.hasOwnProperty(param)) {
			var arr = makeArray(params[param]);
			if(arr.length > 0) {
				ans += param + '=' + arr.join(',') + '&';
			}
		}
	}

	ans = ans.substring(0, ans.length - 1);
	return ans;
}

function makeArray(maybeArr) {
	if(typeof(maybeArr) === 'undefined') {
		return [];
	}

	if(!(maybeArr instanceof Array)) {
		return [maybeArr];
	}

	return maybeArr;
}

function deArray(maybeArr) {
	if(maybeArr.length == 1) {
		return maybeArr[0];
	}

	return maybeArr;
}