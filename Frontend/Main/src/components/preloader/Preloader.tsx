import { Spinner } from "@fluentui/react-components";
import { useTranslation } from "react-i18next";
import styled from "styled-components";

const PreloaderSpinner = styled(Spinner).attrs({size: 'extra-large', labelPosition: 'after'})`
    position: relative;
    top: 40vh;
`;

export const Preloader = () => {
    const {t} = useTranslation();

    return (<PreloaderSpinner label={t('loading')}/>)
}