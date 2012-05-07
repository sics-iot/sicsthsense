/*
	Simple OpenID Plugin
	http://code.google.com/p/openid-selector/
	
	This code is licensed under the New BSD License.
*/

var providers_large = {
	google : {
		name : 'Google',
		url : 'https://www.google.com/accounts/o8/id'
	},
	yahoo : {
		name : 'Yahoo',
		url : 'https://me.yahoo.com/'
	},
	myopenid : {
		name : 'MyOpenID',
		url : 'http://www.myopenid.com/server'
	},
	openid : {
		name : 'OpenID',
		label : 'Enter your OpenID:',
		url : null
	}
};

var providers_small = null

openid.locale = 'en';
openid.sprite = 'en'; // reused in german& japan localization
openid.signin_text = 'Sign-In';
openid.image_title = 'log in with {provider}';
