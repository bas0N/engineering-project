import { Text } from "@fluentui/react-components";
import { useTranslation } from "react-i18next";
import { Page404Container } from "./Page404.styled";

export const Page404 = () => {
    const {t} = useTranslation();

    return (
        <Page404Container>
            <Text as='h1' size={700}>{t('page404.title')}</Text>
            <Text size={400}>{t('page404.description')}</Text>
        </Page404Container>
    )
}