import {bootstrap} from "../node_modules/@angular/platform-browser-dynamic";
import {HTTP_PROVIDERS, XHRBackend} from "@angular/http";
import {provide, enableProdMode} from "@angular/core";
import {AppComponent} from "./app.component";
import {InMemoryBackendService, SEED_DATA} from "angular2-in-memory-web-api";
import {InMemoryDataService} from "./in-memory-data.service";
import {APP_ROUTER_PROVIDERS} from "./routes";
import {disableDeprecatedForms, provideForms} from "@angular/forms";

enableProdMode();

bootstrap(AppComponent, [HTTP_PROVIDERS,
    provide(XHRBackend, {useClass: InMemoryBackendService}), // in-mem server
    provide(SEED_DATA, {useClass: InMemoryDataService}),  // in-mem server data]);
    APP_ROUTER_PROVIDERS,
    disableDeprecatedForms(),
    provideForms()])
    .catch(err => console.error(err));

