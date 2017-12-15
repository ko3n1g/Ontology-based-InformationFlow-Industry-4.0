// This one does sparql stuff

const fusekiProcessor = require('../controller/fusekiProcessor');
const stardogProcessor = require('../controller/stardogProcessor');


const fuseki = "FUSEKI";
const stardog = "STARDOG";

const fs = require('fs');


var core = {
    db: {
        stardog: stardog,
        fuseki: fuseki,
    },
    mode: stardog,
    callback: function () {},
    postProcess: function(var1, var2) {
        var data;

        if (core.mode === fuseki) {
            data = var1.results.bindings;

        } else if (core.mode === stardog) {
            data = var1.body.results.bindings;

        }

        core.callback(data);
    },
    query: {

        init: function (individual) {

            const processor = (core.mode === fuseki)
                ? fusekiProcessor
                : stardogProcessor;

            const file = core.mode.toLowerCase();

            fs.readFile('queries/'+file+'/getIndividuals.sparql', 'utf8', processor.preprocess);
            processor.next = core.postProcess;
            processor.subject = individual;

        },
        properties: function (subject) {

            const processor = (core.mode === fuseki)
                ? fusekiProcessor
                : stardogProcessor;

            const file = core.mode.toLowerCase();
            fs.readFile('queries/'+file+'/getProperties.sparql', 'utf8', processor.preprocess);
            processor.subject = subject;
            processor.next = core.postProcess;

        }
    }
};

module.exports = core;
