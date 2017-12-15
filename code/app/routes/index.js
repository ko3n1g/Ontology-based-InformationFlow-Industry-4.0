var express = require('express');
var router = express.Router();

var sparql = require("../controller/sparql");

sparql.mode = sparql.db.stardog;

router.get('/', function(req, res, next) {
    sparql.query.init('owl:NamedIndividual');
    sparql.callback = function (data) {
        console.log(data);
        res.render('index', {title: 'Express' , result: data, mode: sparql.mode })
    }
});


module.exports = router;
