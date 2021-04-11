module.exports = {
  client: {
    service: {
      name: 'sangria',
      url: 'http://localhost:9000/graphql',
      // optional headers
      headers: {
        authorization: 'Bearer lkjfalkfjadkfjeopknavadf'
      },
      // optional disable SSL validation check
      skipSSLValidation: true
    }
  }
};
