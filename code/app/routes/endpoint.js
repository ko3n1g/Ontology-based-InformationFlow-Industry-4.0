var express = require('express');
var router = express.Router();

var sparql = require("../controller/sparql");
var stardogProcessor = require("../controller/stardogProcessor");


router.get('/', function(req, res, next) {
    var subject = '<' + req.query.subject + '>';
    var mode    = req.query.mode;

    sparql.mode = (mode === sparql.db.stardog)
        ? sparql.db.stardog
        : sparql.db.fuseki;


    sparql.query.properties(subject);
    sparql.callback = function (data) {

        res.render('response-prettyprint-properties', {result: data})
    }
});


module.exports = router;

