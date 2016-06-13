import {Component} from "@angular/core";
import {RouteConfig, ROUTER_DIRECTIVES, ROUTER_PROVIDERS} from "@angular/router-deprecated";
import {TicketService} from "./ticket.service";
import {TicketsComponent} from "./tickets.component";
import {DashboardComponent} from "./dashboard.component";
import {TicketDetailComponent} from "./ticket-detail.component";

@Component({
    selector: 'my-app',
    templateUrl: 'app/templates/app.component.html',
    directives: [
        ROUTER_DIRECTIVES],
    providers: [
        ROUTER_PROVIDERS,
        TicketService
    ]
})

@RouteConfig([{
    path: '/dashboard',
    name: 'Dashboard',
    component: DashboardComponent,
    useAsDefault: true
}, {
    path: '/tickets',
    name: 'Tickets',
    component: TicketsComponent
}, {
    path: '/tickets/:id',
    name: 'TicketDetail',
    component: TicketDetailComponent
}])

export class AppComponent {
    title = 'Dimo Admin Panel';
}
