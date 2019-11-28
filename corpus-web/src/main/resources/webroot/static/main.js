(function ($) {
    'use strict';

    const makePoetryHtml = function (obj) {
        return `<div class="col-md-8"><div class="mx-auto mt-2 card shadow"><div class="poetry-top-banner"></div><div class="card-body">
<h5 class="card-title text-center">${preprocessString(obj.title)}</h5>
<h6 class="card-subtitle text-secondary text-center">${preprocessString(obj.subtitle)}</h6>
<h6 class="card-text mb-2 text-info text-center">${preprocessString(obj.dynasty)}&nbsp;○&nbsp;${preprocessString(obj.author)}</h6>
<p class="card-text text-justify">${preprocessString(obj.content)}</p>
</div></div></div>`;
    };

    const preprocessString = function (s) {
        if (!s || s.length === 0) {
            return '•';
        }
        return s;
    };

    const $searchResultBox = $('#search-result-box');

    const renderSearchResult = function (a) {
        if (!Array.isArray(a) || a.length === 0) {
            $searchResultBox.empty();
            return;
        }
        const htmlList = [];
        for (let i = 0; i < a.length; ++i) {
            htmlList.push(makePoetryHtml(a[i]));
        }

        $searchResultBox.prev('img').remove();
        $searchResultBox.html(htmlList.join(''));
    };

    const queryThenRender = function (query) {
        $.post('api/query-poetry', {
            query: query,
            preTag: '<span class="text-danger">',
            postTag: '</span>',
            maxSize: 10
        }).done(function (data) {
            if (data.status === 0) {
                renderSearchResult(data.documents);
            } else {
                console.error(data.msg);
            }
        });
    };

    $(document).ready(function () {

        const $searchInput1 = $('#search-input1');
        const $searchBtn1 = $('#search-btn1');

        $searchBtn1.on('click', function () {
            const query = $searchInput1.val();
            if (query === '') {
                $searchInput1.focus();
                return;
            }
            queryThenRender(query);
        });

        $(document).keyup(function (event) {
            if (event.keyCode === 13) {
                $searchBtn1.trigger("click");
            }
        });
    });


})(window.jQuery);