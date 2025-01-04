export type User = {
    id: number;
    uuid: string;
    email: string;
    imageUrl: string | null;
    firstName: string | null;
    lastName: string | null;
    role: string;
    displayScore: number;
};

export const nullableStringsComparator = (a: User, b: User, property: keyof User) => 
  a[property as keyof User] !== null && b [property as keyof User] !== null 
  ? (a[property as keyof User] as string).localeCompare(b[property as keyof User] as string)  : 
    a[property as keyof User] === null && b.firstName !== null 
    ? -1 : a[property as keyof User] !== null && b.firstName === null 
    ? 1 : 0;

export const modifyTableText = (text: string, maxLength: number) => text.length > maxLength ? text.substring(0, maxLength)+'...' : text;
