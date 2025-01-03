import { AddressRequest } from "../Order/Order.types";
import { Input, Label, Text } from "@fluentui/react-components";
import { AddressFormContainer } from "./AddressForm.styled";
import { useTranslation } from "react-i18next";

export interface AddressFormProps {
    address: AddressRequest;
    setAddress: (addr: AddressRequest) => void;
    labelStreet: string;
    labelCity: string;
    labelState: string;
    labelPostalCode: string;
    labelCountry: string;
}

export const AddressForm = ({ 
    address, 
    setAddress, 
    labelStreet, 
    labelCity, 
    labelState, 
    labelPostalCode, 
    labelCountry 
}: AddressFormProps) => {
    const {t} = useTranslation();

    return (
        <AddressFormContainer>
            <Text weight="semibold" block>
                {t('order.address.everythingRequired')}
            </Text>

            <Label id={labelStreet} required>{labelStreet}</Label>
            <Input
                required
                value={address.street || ""}
                onChange={(e) => setAddress({ ...address, street: e.target.value })}
                placeholder={t('order.address.streetPlaceholder')}
                aria-labelledby={labelStreet}
            />

            <Label id={labelCity} required>{labelCity}</Label>
            <Input
                required
                value={address.city || ""}
                onChange={(e) => setAddress({ ...address, city: e.target.value })}
                placeholder={t('order.address.cityPlaceholder')}
                aria-labelledby={labelCity}
            />

            <Label id={labelState} required>{labelState}</Label>
            <Input
                required
                value={address.state || ""}
                onChange={(e) => setAddress({ ...address, state: e.target.value })}
                placeholder={t('order.address.statePlaceholder')}
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
                placeholder={t('order.address.countryPlaceholder')}
                aria-labelledby={labelCountry}
            />
        </AddressFormContainer>
    );
}