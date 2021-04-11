# Scala / Sangria / Apollo starter
Starter project for a Scala / Sangria backend and Angular / Apollo Graphql client frontend with Mongo persistent data store

## Graphql Schema Typescript generation for Frontend
The frontend project can automatically generate Typescript interfaces for all the queries it requests. The project is configured
to pull the Graphql schema from the backend and generate interfaces by running the command `npm run-script generateInterfaces` 
from the `./frontend` directory. 

See [Apollo Client generate types tutorial ](https://www.apollographql.com/blog/typescript-graphql-code-generator-generate-graphql-types-with-apollo-codegen-tutorial/) for more info.


## Project Structure
    ./                          # project root
    backend/                    # Scala / Sangria backend
    | -- Readme.md                  # Backend readme
    frontend/                   # Angular / Apollo frontend
    | -- Readme.md                  # Frontend readme
    docker/                     # Docker files
    ./README.md                 # This readme file 

## Resources
[Getting Started with Sangria](https://sangria-graphql.github.io/getting-started/)
[Introduction Apollo Angular](https://apollo-angular.com/docs/)
[Introduction to Graphql](https://graphql.org/learn/)