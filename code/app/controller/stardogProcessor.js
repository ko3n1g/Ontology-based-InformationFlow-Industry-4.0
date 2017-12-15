String.prototype.replaceAll = function(search, replacement) {
    var target = this;
    return target.split(search).join(replacement);
};


var stardogProcessor = {
    preprocess: function (err, data) {

        stardogProcessor.fire(data);
    },
    fire: function (sparql) {
        const { Connection, query } = require('stardog');

        const conn = new Connection({
            username: 'admin',
            password: 'admin',
            endpoint: 'http://localhost:5820',
        });

        const subject = stardogProcessor.subject;
        if (subject)
            sparql = sparql.replaceAll("XXX", subject);

        console.log(sparql);

        query.execute(conn, 'oml', sparql, {limit: 10, offset: 0}, { reasoning: false })
            .then(stardogProcessor.next);

    },
    subject: "",

    next: function() {}

};

module.exports = stardogProcessor;