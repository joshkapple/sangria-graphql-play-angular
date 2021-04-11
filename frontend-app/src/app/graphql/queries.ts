import {gql} from "@apollo/client/core";

export const GET_ALL_CHARACTERS = gql`query getAllCharacters{all{name id}}`
