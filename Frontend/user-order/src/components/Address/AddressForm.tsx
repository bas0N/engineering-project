import {AddressRequest} from "../../Order.types";
import {Input, Label, Text} from "@fluentui/react-components";
import {useAddressFormStyles} from "./AddressForm.styled";

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

            <Label id={labelStreet} required>{labelStreet}</Label>
            <Input
                required
                value={address.street || ""}
                onChange={(e) => setAddress({ ...address, street: e.target.value })}
                placeholder="Enter street"
                aria-labelledby={labelStreet}
            />

            <Label id={labelCity} required>{labelCity}</Label>
            <Input
                required
                value={address.city || ""}
                onChange={(e) => setAddress({ ...address, city: e.target.value })}
                placeholder="Enter city"
                aria-labelledby={labelCity}
            />

            <Label id={labelState} required>{labelState}</Label>
            <Input
                required
                value={address.state || ""}
                onChange={(e) => setAddress({ ...address, state: e.target.value })}
                placeholder="Enter state"
                aria-labelledby={labelState}
            />

            <Label id={labelPostalCode} required>{labelPostalCode}</Label>
            <Input
                required
                value={address.postalCode || ""}
                onChange={(e) => setAddress({ ...address, postalCode: e.target.value })}
                placeholder="xx-xxx"
                pattern="\d{2}-\d{3}"
                aria-labelledby={labelPostalCode}
            />

            <Label id={labelCountry} required>{labelCountry}</Label>
            <Input
                required
                value={address.country || ""}
                onChange={(e) => setAddress({ ...address, country: e.target.value })}
                placeholder="Enter country"
                aria-labelledby={labelCountry}
            />
        </div>
    );
}