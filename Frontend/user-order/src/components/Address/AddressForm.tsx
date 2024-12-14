import {AddressRequest} from "../../Order.types.ts";
import {Input, Label, Text} from "@fluentui/react-components";
import {useAddressFormStyles} from "./AddressForm.styled.tsx";

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
    const styles = useAddressFormStyles();

    return (
        <div className={styles.container}>
            <Text weight="semibold" block>
                All fields are required.
            </Text>

            <Label required>{labelStreet}</Label>
            <Input
                required
                value={address.street || ""}
                onChange={(e) => setAddress({ ...address, street: e.target.value })}
                placeholder="Enter street"
            />

            <Label required>{labelCity}</Label>
            <Input
                required
                value={address.city || ""}
                onChange={(e) => setAddress({ ...address, city: e.target.value })}
                placeholder="Enter city"
            />

            <Label required>{labelState}</Label>
            <Input
                required
                value={address.state || ""}
                onChange={(e) => setAddress({ ...address, state: e.target.value })}
                placeholder="Enter state"
            />

            <Label required>{labelPostalCode}</Label>
            <Input
                required
                value={address.postalCode || ""}
                onChange={(e) => setAddress({ ...address, postalCode: e.target.value })}
                placeholder="xx-xxx"
                pattern="\d{2}-\d{3}"
                title="Please enter postal code in format xx-xxx (e.g. 00-123)"
            />

            <Label required>{labelCountry}</Label>
            <Input
                required
                value={address.country || ""}
                onChange={(e) => setAddress({ ...address, country: e.target.value })}
                placeholder="Enter country"
            />
        </div>
    );
}