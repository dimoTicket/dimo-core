import {Injectable} from "@angular/core";

@Injectable()
export class ImageService {

    private imagesUrl = "";

    //Constructs the image URL for a specific ticket
    getImageUrlForTicketId(id:number):string {
        return "";
    }
}