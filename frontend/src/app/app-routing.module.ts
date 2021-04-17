import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import {PlaygroundWrapperComponent} from './playground-wrapper/playground-wrapper.component'
import {HomeComponent} from './home/home.component'
import {CharactersComponent} from "./characters/characters.component"

const routes: Routes = [
    {path: '', component: HomeComponent},
    {path: 'playground', component: PlaygroundWrapperComponent},
    {path: 'characters', component: CharactersComponent}
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
