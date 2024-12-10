import {AddressRequest} from "../../Order.types.ts";
import {Input, Label} from "@fluentui/react-components";

interface AddressFormProps {
    address: AddressRequest;
    setAddress: (addr: AddressRequest) => void;
    labelStreet: string;
    labelCity: string;
    labelState: string;
    labelPostalCode: string;
    labelCountry: string;
}

export function AddressForm({ address, setAddress, labelStreet, labelCity, labelState, labelPostalCode, labelCountry }: AddressFormProps) {
    return (
        <div>
            <Label>{labelStreet}</Label>
            <Input
                value={address.street || ""}
                onChange={(e) => setAddress({ ...address, street: e.target.value })}
                placeholder="Enter street"
            />

            <Label>{labelCity}</Label>
            <Input
                value={address.city || ""}
                onChange={(e) => setAddress({ ...address, city: e.target.value })}
                placeholder="Enter city"
            />

            <Label>{labelState}</Label>
            <Input
                value={address.state || ""}
                onChange={(e) => setAddress({ ...address, state: e.target.value })}
                placeholder="Enter state"
            />

            <Label>{labelPostalCode}</Label>
            <Input
                value={address.postalCode || ""}
                onChange={(e) => setAddress({ ...address, postalCode: e.target.value })}
                placeholder="Enter postal code"
            />

            <Label>{labelCountry}</Label>
            <Input
                value={address.country || ""}
                onChange={(e) => setAddress({ ...address, country: e.target.value })}
                placeholder="Enter country"
            />
        </div>
    );
}