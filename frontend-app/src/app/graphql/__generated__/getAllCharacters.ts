/* tslint:disable */
/* eslint-disable */
// @generated
// This file was automatically generated and should not be edited.

// ====================================================
// GraphQL query operation: getAllCharacters
// ====================================================

export interface getAllCharacters_all {
  __typename: "Droid" | "Human" | "Jedi";
  /**
   * The name of the character.
   */
  name: string | null;
  /**
   * The id of the character.
   */
  id: string;
}

export interface getAllCharacters {
  all: getAllCharacters_all[];
}
