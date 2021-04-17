import {Component, Inject, Input, OnInit} from '@angular/core'
import {Apollo} from "apollo-angular"
import {gql} from "@apollo/client/core"
import {GET_ALL_CHARACTERS} from "../graphql/queries"
import {MatDialog} from '@angular/material/dialog'
import {MAT_DIALOG_DATA} from '@angular/material/dialog'
import {characters_all} from "../graphql/__generated__/characters";

@Component({
  selector: 'app-characters',
  templateUrl: './characters.component.html',
  styleUrls: ['./characters.component.scss']
})
export class CharactersComponent implements OnInit {
  characters: characters_all[] = []
  loading = true
  error: any

  constructor(private apollo: Apollo, public dialog: MatDialog) {}

  openDialog(character: characters_all): void {
    const dialogRef = this.dialog.open(DialogDeleteComponent, {data: {character}})

    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`)
    })
  }

  ngOnInit(): void {
    this.apollo.watchQuery({
      query: GET_ALL_CHARACTERS
    }).valueChanges.subscribe((result: any) => {
      this.characters = result?.data?.all
      this.loading = result.loading
      this.error = result.error
    })
  }
}

@Component({
  selector: 'app-dialog-delete',
  templateUrl: 'dialog-delete.component.html',
})
export class DialogDeleteComponent {
  character: characters_all

  constructor(@Inject(MAT_DIALOG_DATA) public data: { character: characters_all }) {
    this.character = data.character
  }
}

