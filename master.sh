#!/usr/bin/env bash

function echoYell() {
    echo -e "\033[0;33m$1\033[0m"
}

function echoBlue() {
    echo -e "\033[0;34m$1\033[0m"
}

function echoRed() {
    echo -e "\033[0;31m$1\033[0m"
}

function cmdCleanIde() {
    rm -rf .idea
    find . -name \*.iml -type f -delete
}

function cmdLocalize() {
    if [[ ! -d "./sheets-localizations-generator" ]]
    then
        git clone git@gitlab.icerockdev.com:scl/sheets-localizations-generator.git
        cd sheets-localizations-generator
        npm install
    else
        cd sheets-localizations-generator
    fi

    npm start android strings "1b8uFSnBE6fQ2jhiSikLCaeSwG2Kv_nlNToEP3Zfbn5I" 'platform!A1:C' ../android-app/src/main/res/
    npm start mpp strings "1b8uFSnBE6fQ2jhiSikLCaeSwG2Kv_nlNToEP3Zfbn5I" 'mpp!A1:C' ../mpp-library/src/commonMain/resources/MR/
#    npm start mpp plurals "1b8uFSnBE6fQ2jhiSikLCaeSwG2Kv_nlNToEP3Zfbn5I" 'mpp-plurals!A1:D' ../mpp-library/src/commonMain/resources/MR/
    npm start ios strings "1b8uFSnBE6fQ2jhiSikLCaeSwG2Kv_nlNToEP3Zfbn5I" 'platform!A1:C' ../ios-app/src/Resources/
}


function cmdGraph() {
    ./gradlew :android-app:projectDependencyGraph
}

function cmdHelp {
    echoYell 'Help'
    echoBlue 'Usage:'
    echoYell '  master.sh COMMAND <params>'
    echoYell ''
    echoBlue 'Commands:'
    echoYell '  help                            Помощь'
    echoYell '  clean_ide                       Удаление файлов конфигурации проекта IDEA'
    echoYell '  init                            Инициализация проекта из бойлерплейта'
    echoYell '  localize                        Сгенерировать файлы локализации из gSheets'
    echoYell '  graph                           Сгенерировать граф модулей'
}

function run() {
    COMMAND=$1
    case "$COMMAND" in
        clean_ide)
            echoBlue 'Удаление конфигурационных файлов IDEA (для корректной работы проект должен быть закрыт)'
            cmdCleanIde
        ;;
        init)
            shift
            cmdInit $@
        ;;
        localize)
            cmdLocalize
        ;;
        graph)
            cmdGraph
        ;;
        help|*)
            cmdHelp
        ;;
    esac
}

run $@
