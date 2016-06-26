import {provideRouter, RouterConfig} from "@angular/router";
import {TicketsComponent} from "./tickets.component";
import {TicketDetailComponent} from "./ticket-detail.component";
import {DashboardComponent} from "./dashboard.component";

export const appRoutes:RouterConfig = [
    {
        path: '',
        component: DashboardComponent,
        terminal: true
    }, {
        path: 'tickets',
        component: TicketsComponent
    }, {
        path: 'ticket/:id',
        component: TicketDetailComponent
    }
];

export const APP_ROUTER_PROVIDERS = provideRouter(appRoutes);