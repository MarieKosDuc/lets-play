export class Ad {
  id!: number;
  createdAt!: Date;
  postedByName!: string;
  postedById!: string;
  title!: string;
  seekingMusicianType!: string;
  image: string | undefined;
  styles!: string[];
  location!: string;
  description!: string;

  constructor(
    id: number,
    createdAt: Date,
    postedByName: string,
    postedById: string,
    title: string,
    seekingMusicianType: string,
    image: string | undefined,
    styles: string[],
    location: string,
    description: string
  ) {
    this.id = id;
    this.createdAt = createdAt;
    this.postedByName = postedByName;
    this.postedById = postedById;
    this.title = title;
    this.seekingMusicianType = seekingMusicianType;
    this.image = image;
    this.styles = styles;
    this.location = location;
    this.description = description;
  }
}
