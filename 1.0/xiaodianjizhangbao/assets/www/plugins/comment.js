/**
 * 
 * Phonegap share plugin for Android
 * Kevin Schaul 2011
 *
 */

var Comment = function() {};

Comment.prototype.show = function() {
    return cordova.exec(null, null, 'Comment', 'show', []);
};

if(!window.plugins) {
    window.plugins = {};
}
if (!window.plugins.comment) {
    window.plugins.comment = new Comment();
}
