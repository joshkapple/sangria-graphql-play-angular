/* tslint:disable */
/* eslint-disable */
// @generated
// This file was automatically generated and should not be edited.

import { Episode } from "./../../../../__generated__/globalTypes";

// ====================================================
// GraphQL query operation: characters
// ====================================================

export interface characters_all_friends {
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

export interface characters_all {
  __typename: "Droid" | "Human" | "Jedi";
  /**
   * The name of the character.
   */
  name: string | null;
  /**
   * The id of the character.
   */
  id: string;
  /**
   * The friends of the character, or an empty list if they have none.
   */
  friends: characters_all_friends[];
  /**
   * Which movies they appear in.
   */
  appearsIn: (Episode | null)[] | null;
}

export interface characters {
  all: characters_all[];
}
