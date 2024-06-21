import { User } from "src/app/authentication/models/user.model";
import { Ad } from "./ad.model";
import { stringOrNumber } from "@cloudinary/url-gen/types/types";

export interface contactEmail {
    ad: Ad;
    fromUser: string;
    toUser: stringOrNumber;
    messageContent: string;
}