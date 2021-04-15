import {Component, OnInit} from '@angular/core';
import {Apollo} from "apollo-angular";
import {gql} from "@apollo/client/core";
import {GET_ALL_CHARACTERS} from "../graphql/queries";
import {getAllCharacters_all} from "../graphql/__generated__/getAllCharacters";

@Component({
  selector: 'app-characters',
  templateUrl: './characters.component.html',
  styleUrls: ['./characters.component.css']
})
export class CharactersComponent implements OnInit {
  characters: getAllCharacters_all[] | undefined
  loading = true
  error: any

  constructor(private apollo: Apollo) {
  }

  ngOnInit(): void {
    console.log("i inited!")
    this.apollo.watchQuery({
      query: GET_ALL_CHARACTERS
    }).valueChanges.subscribe((result: any) => {
      this.characters = result?.data?.all
      this.loading = result.loading
      this.error = result.error
    })
  }

}
