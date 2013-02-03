function RecordingsManager () {
	var recordings = [];

	/**
	 * Left pad a value with spaces.
	 */
	function leftPad (s, count) {
		if (s.length >= count) {
			return s;
		}
		while (s.length < count) {
			s = " " + s;
		}
		return s;
	}

	/**
	 * Comparator function to compare by recording time.
	 */
	function compareByTime (a, b) {
		return a.time > b.time;
	}

	/**
	 * 
	 */
	function showRecordingsWithDelta (recordings, $el) {
		function tr (tag) {
			var $tr = $("<tr/>");
			for (var i = 1; i < arguments.length; i++) {
				$tr.append($("<" + tag + "/>").html(arguments[i]));
			}
			return $tr;
		}

		var $table = $("<table class=recordings/>");
		var $tbody = $("<tbody/>").appendTo($table);
		$tbody.append(tr("th", "", "time", "delta", "delta", "msg"));

		// create an array with the new output text
		$.each(recordings, function (idx, r) {
			var r = recordings[idx];
			var delta = idx > 0 ? r.time - recordings[idx-1].time : 0;
			var $tr = tr("td", "", r.time, delta, "", r.msg, "");
			$tr.attr("data-idx", idx).addClass("recording");
			var $tds = $tr.find("td");
			$tds.eq(0).append($("<input type=checkbox />")).addClass("select");
			$tds.eq(1).addClass("time");
			$tds.eq(2).addClass("delta");
			$tds.eq(3).addClass("delta");
			$tds.eq(4).addClass("msg");
			$tds.eq(5).append($("<input type=checkbox />")).addClass("select");
			$tbody.append($tr);
		});

		$el.append($table);
	}

	/**
	 * Show the total time between the last and first Recordings.
	 */
	function total (recordings, $el) {
		var total = recordings[recordings.length-1].time - recordings[0].time;
		$el.html(total + "ms");
	}

	/**
	 * Load the recordings and place them in the page.
	 */
	function load (url, $content) {
		$content.html("");
		return $.getJSON(url).success(function (data, textStatus, jqXHR) {
			recordings = data.recordings;
			recordings.sort(compareByTime);

			showRecordingsWithDelta(recordings , $content);
			total(recordings, $("<div/>").appendTo($content));
		}).error(function (jqXHR, textStatus, errorThrown) {
			alert([url, textStatus, errorThrown].join("\n"));
		});
	}

	/**
	 *
	 */
	function recalcSelectedDeltas (recordings, $table) {
		var $trs = $table.find("tr.recording");
		var lastTime = recordings[0].time;
		for (var i = 0; i < recordings.length; i++) {
			var r = recordings[i];
			var $tr = $trs.eq(i);
			var $delta = $tr.find("td.delta").eq(1);
			var $checkboxes = $tr.find("input[type=checkbox]");
			if (r.selected) {
				$delta.html(r.time - lastTime);
				$tr.addClass("selected");
				$checkboxes.prop("checked", true);
				lastTime = r.time;
			} else {
				$delta.html("");
				$tr.removeClass("selected");
				$checkboxes.prop("checked", false);
			}
		}
	}

	function tableClickListener (evt) {
		function isSelectionEvent (evt) {
			var $t = $(evt.target);
			return $t.is(":checkbox");
		}

		if (!isSelectionEvent(evt)) {
			return;
		}

		var $t = $(evt.target);
		var $tr = $t.closest("tr[data-idx]");
		if ($tr.length < 1) {
			return;
		}

		var idx = parseInt($tr.attr("data-idx"), 10);
		var recording = recordings[idx];
		if (!recording) {
			return;
		}

		// toggle selection
		recording.selected = true === recording.selected ? false: true;

		// recalc
		recalcSelectedDeltas(recordings, $tr.closest("table"));
	}

	// Add a click listener for clicks on the recordings table.
	// These clicks will allow the user to manually select those recordings
	// they want time deltas for.
	$(document.body).on("click", "table.recordings", tableClickListener);

	this.load = load;
}
