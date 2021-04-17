import {gql} from "@apollo/client/core"

export const GET_ALL_CHARACTERS = gql`query characters{all{name id friends {name id} appearsIn}}`
