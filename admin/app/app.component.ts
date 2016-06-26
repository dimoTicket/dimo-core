import {Component} from "@angular/core";
import {ROUTER_DIRECTIVES} from "@angular/router";
import {TicketService} from "./ticket.service";
import "./rxjs-operators";


@Component({
    selector: 'my-app',
    templateUrl: 'app/templates/app.component.html',
    directives: [
        ROUTER_DIRECTIVES],
    providers: [
        TicketService
    ]
})

export class AppComponent {
}
