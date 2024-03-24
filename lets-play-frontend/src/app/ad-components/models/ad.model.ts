export class Ad {
    id!: number;
    createdAt!: Date;
    title!: string;
    userType!: string;
    seeking!: string;
    image: string | undefined;
    style!: string;
    description!: string;

    constructor(id: number, createdAt: Date, title: string, userType: string, seeking: string, image: string | undefined, style: string, description: string) {
        this.id = id;
        this.createdAt = createdAt;
        this.title = title;
        this.userType = userType;
        this.seeking = seeking;
        this.image = image;
        this.style = style;
        this.description = description;
    }
}

