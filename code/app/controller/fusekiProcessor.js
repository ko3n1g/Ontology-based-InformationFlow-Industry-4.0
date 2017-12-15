
var fusekiProcessor = {
    preprocess: function (err, data) {
        fusekiProcessor.fire(data);
    },
    fire: function (query, subject) {

        var SparqlClient = require('sparql-client');
        var endpoint = 'http://localhost:3030/OML.complete/query';

        var client = new SparqlClient(endpoint);

        client.query(query);
        var subject = fusekiProcessor.subject;
        if (subject) {
            client.bind('s', '<' + subject + '>')
        }

        client.execute(fusekiProcessor.next);


    },
    subject: null,
    next: function() {}
};

module.exports = fusekiProcessor;
